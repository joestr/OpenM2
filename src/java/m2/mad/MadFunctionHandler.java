/*
 * Class: MadFunctionHandler.java
 */

// package:
package m2.mad;

// imports:
import ibs.app.AppFunctions;
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.service.user.User;
import ibs.util.list.IElementId;

import m2.mad.Register_01;


//KR TODO: The login wizard is currently not used.
// So this functionality is commented out.
/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: MadFunctionHandler.java,v 1.63 2007/07/23 08:21:36 kreimueller Exp $
 *
 * @author      Klaus Reimüller (KR), 980402
 ******************************************************************************
 */
public class MadFunctionHandler extends GeneralFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MadFunctionHandler.java,v 1.63 2007/07/23 08:21:36 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a MadFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public MadFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // MadFunctionHandler


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Covers all functions the Login-Wizard has to perform. <BR/>
     *
     * @param   function    The function to be called.
     * @param   values      The values for the function handler.
     */
    public void loginWizard (int function, FunctionValues values)
    {
        int functionLocal = function; // variable for local assignments
        User user = values.getUser ();

        if (functionLocal == AppFunctions.FCT_NOFUNCTION)
        {
            functionLocal = MadFunctions.FCT_SHOWFORM;
        } // if
        BusinessObject reg = new Register_01 ();
        OID temp = null;
        reg.initObject (OID.getEmptyOid (), user, values.p_env, values.p_sess,
            values.p_app);
        int representationForm = 1;

        switch (functionLocal)          // perform function
        {
            case MadFunctions.FCT_SHOWFORM:
                reg.showChangeForm (representationForm);
                break;

            case MadFunctions.FCT_CREATEOBJECTS:
                reg.getParameters ();
                temp = reg.createActive (Operations.OP_NEW);
                if (temp != null)
                {
                    // all done, redirect to the frameset
                    // retrieve new Data of User
                    user.fullname = ((Register_01) reg).aname;
                    // registration finished:
                    values.p_sess.wizardRegistration = false;
                    this.evalFunction (AppFunctions.FCT_LAYOUTPERFORM,
                        values.p_oid, values);
                } // if
                else
                {
                    reg.showChangeForm (representationForm);
                } // else
                break;
            default: // nothing to do
        } // switch
    } // loginWizard


    /**************************************************************************
     * Perform login and show main page. <BR/>
     * This method sets the private menu tab to null to ensure that it is
     * loaded from the database instead of cache.
     *
     * @param   values      The values for the function handler.
     */
/*
    protected void login (FunctionValues values)
    {
        // cleans the vector:
        values.p_sess.menus = null;

        // perform login within super class:
        super.login (values);

        // check if we need to start the registration wizard
        if (values.p_sess.wizardRegistration)
        {
            // now start the registration wizard
            // the form for the registering has to be shown
            loginWizard (MadFunctions.FCT_SHOWFORM, values);
        } // if (values.p_sess.wizardRegistration)
    } // login
*/

} // class MadFunctionHandler
