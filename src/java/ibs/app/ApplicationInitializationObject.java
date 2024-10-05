/*
 * class ApplicationInitializationObject
 */

// package:
package ibs.app;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;


/******************************************************************************
 * This class accesses the DB on ApplicationStart. <BR/>
 *
 * @version     $Id: ApplicationInitializationObject.java,v 1.9 2013/01/16 16:14:11 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 011031
 *
 * @deprecated  This object is never used.
 ******************************************************************************
 */
public class ApplicationInitializationObject extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ApplicationInitializationObject.java,v 1.9 2013/01/16 16:14:11 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constants
    ///////////////////////////////////////////////////////////////////////////

    /**
     * DB-Procedure to call. <BR/>
     */
    public static final String PROC_INITIALIZE = "p_Application$initialize";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ApplicationInitializationObject object. <BR/>
     *
     * @param   sessioninfo The actual session info.
     * @param   info        The global application info.
     */
    public ApplicationInitializationObject (SessionInfo sessioninfo,
                                            ApplicationInfo info)
    {
        this.sess = sessioninfo;
        this.app = info;
    } // ApplicationInitializationObject


    /**************************************************************************
     * Initializes a business object. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Get a business object out of the database. <BR/>
     * This method checks if the object was already loaded into memory. In this
     * case it checks if the user's rights are sufficient to perform the
     * requested operation on the object. If this is all right the object is
     * returned otherwise an exception is raised. <BR/>
     * If the object is not already in the memory it must be loaded from the
     * database. In this case there is also a rights check done. If this is all
     * right the object is returned otherwise an exception is raised. <BR/>
     */
    public final void initialize ()
    {
        // create the stored procedure call:
        StoredProcedure sp = new StoredProcedure(
                ApplicationInitializationObject.PROC_INITIALIZE,
                StoredProcedureConstants.RETURN_VALUE);

        // operation
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);

//trace ("KR before call function data");
        try
        {
            // perform the function call:
            BOHelpers.performCallFunctionData(sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
//trace ("KR after call function data");
    } // initialize

} // ApplicationInitializationObject
