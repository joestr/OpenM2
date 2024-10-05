/*
 * Class: VersionFunctionHandler.java
 */

// package:
package m2.version;

// imports:
import ibs.app.AppFunctions;
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Operations;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.ButtonBarElement;
import ibs.util.NoAccessException;
import ibs.util.list.IElementId;

import m2.version.publish.PublicationMessages;


/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: VersionFunctionHandler.java,v 1.66 2010/04/07 13:37:14 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980402
 ******************************************************************************
 */
public class VersionFunctionHandler extends GeneralFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: VersionFunctionHandler.java,v 1.66 2010/04/07 13:37:14 rburgermann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a VersionFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public VersionFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // VersionFunctionHandler


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed. <BR/>
     *          <CODE>AppFunctions.FCT_NOFUNCTION</CODE> if there is no function
     *          or the function was already performed.
     */
    public int evalFunction (int function, FunctionValues values)
    {
        switch (function)               // perform function
        {
            case VersionFunctions.FCT_PUBLISH:
                this.publish (values);
                break;

            default:            // unknown function
                // return the function to be performed:
                return function;
        } // switch function
//trace ("VersionFunctionHandler.evalFunction end");

        // return that no further function shall be performed:
        return AppFunctions.FCT_NOFUNCTION;
    } // evalFunction


    /**************************************************************************
     * Method to publish a publishable version object. <BR/>
     *
     * @param   values  The values for the function handler.
     */
    protected void publish (FunctionValues values)
    {
        BusinessObject obj;             // the business object

        if ((obj = values.getObject ()) != null)
        {
            // execute the function:
            try
            {
                // show the popup message
                if (!(obj instanceof  Container) && obj.publish ())
                {
                    IOHelpers.showPopupMessage (PublicationMessages.MSG_OBJECTPUBLISHED,
                        null, values.p_app, values.p_sess, values.p_env);
                } // if

                // if no error has occurred, display object
                this.evalFunction (AppFunctions.FCT_SHOWOBJECT, obj.oid, values);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                obj.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider
                .getMessage(BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, values.p_env),
                values.p_app, values.p_sess, values.p_env);
        } // else
    } // publish


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     */
    public void setButtons (ButtonBarElement buttonBar)
    {
        // call corresponding method of super class:
        super.setButtons (buttonBar);

        // publish
        this.setButton (buttonBar, Buttons.BTN_PUBLISH,
                   "top.load (" + VersionFunctions.FCT_PUBLISH + ")",
                   Operations.OP_CHANGE, 0);
    } // setButtons

} // class VersionFunctionHandler
