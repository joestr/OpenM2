/*
 * Class: ExportInterface.java
 */

// package:
package ibs.di.exp;

// imports:
//TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.DITokens;
import ibs.di.XMLViewer_01;
//TODO: unsauber
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;


/******************************************************************************
 * This class shows the representation form for the export interface. It is
 * there for resolve multilang problems. <BR/>
 *
 * @version     $Id: ExportInterface.java,v 1.8 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Angelika Luschin (AN), 990104
 *
 * @see         ibs.di.imp.ImportIntegrator
 ******************************************************************************
 */
public class ExportInterface extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExportInterface.java,v 1.8 2010/04/07 13:37:16 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class ImportInterface. <BR/>
     */
    public ExportInterface ()
    {
        // call constructor of super class XMLViewer
        super ();
        // initialize properties common to all subclasses:
    } // constructor importInterface


    /**************************************************************************
     * This constructor creates a new instance of the class ImportContainer. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ExportInterface (OID oid, User user)
    {
        // call constructor of super class XMLViewer
        super (oid, user);

        // initialize properties common to all subclasses:
    } // constructor of the exportinterface


    /**************************************************************************
     * This method transforms a string into a valid token that can be displayed
     * in a form. <BR/>
     *
     * @param field ... the namme of a field to be transformed into an argument
     *
     * @return  a valid token to be used ni a form
     */
    protected String createToken (String field)
    {
        // get the fieldname of the connector and return the token
        if (field.equals (DIConstants.INTERFACE_CONNECTOR))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_CONNECTOR, env);
        } // if
        // get the fieldname of the filter and return the token
        else if (field.equals (DIConstants.INTERFACE_EXPORT_FILTER))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_FILTER, env);
        } // else if
        // get the fieldname of isExportContainer and return the token
        else if (field.equals (DIConstants.INTERFACE_EXPORTCONTAINER))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_EXPORTCONTAINER, env);
        } // else if
        // get the fieldname of isHierarchical export and return the token
        else if (field.equals (DIConstants.INTERFACE_HIERARCHICAL))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_HIERARCHICAL, env);
        } // else if
        // get the fieldname of hierarchicalinfo and return the token
        else if (field.equals (DIConstants.INTERFACE_HIERARCHICALINFO))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_HIERARCHICALEXPORTNOTE, env);
        } // else if
        // get the fieldname of isKeyMapping and return the token
        else if (field.equals (DIConstants.INTERFACE_KEYMAPPING))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_KEYMAPPING, env);
        } // else if
        // get the fieldname of isPrettyPrint and return the token
        else if (field.equals (DIConstants.INTERFACE_PRETTYPRINT))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_PRETTYPRINT, env);
        } // else if
        // get the fieldname of isRecursive and return the token
        else if (field.equals (DIConstants.INTERFACE_RECURSIVE))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_RECURSIVE, env);
        } // else if
        // get the fieldname of isSinglefile and return the token
        else if (field.equals (DIConstants.INTERFACE_SINGLEFILE))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_SINGLEFILE, env);
        } // else if
        // get the fieldname of translator and return the token
        else if (field.equals (DIConstants.INTERFACE_TRANSLATOR))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_INTERFACE_TRANSLATOR, env);
        } // else if
        // get the name for the log settings
        // get the fieldname for append log
        else if (field.equals (DIConstants.LOG_APPEND))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_APPENDLOGFILE, env);
        } // else if
        // get the fieldname for display log
        else if (field.equals (DIConstants.LOG_DISPLAY))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_DISPLAYLOGFILE, env);
        } // else if
        // get the fieldname for save log
        else if (field.equals (DIConstants.LOG_SAVE))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_WRITELOGFILE, env);
        } // else if
        // get the fieldname for name of the log
        else if (field.equals (DIConstants.LOG_NAME))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_LOGFILENAME, env);
        } // else if
        // get the fieldname for path of the log
        else if (field.equals (DIConstants.LOG_PATH))
        {
            return MultilingualTextProvider.getText (DITokens.TOK_BUNDLE,
                DITokens.ML_LOGFILEPATH, env);
        } // else if
        else
        {
            return field;
        } // else
    } // ceateToken

} // class ExportInterface
