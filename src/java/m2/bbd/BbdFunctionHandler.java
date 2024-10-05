/*
 * Class: BbdFunctionHandler.java
 */

// package:
package m2.bbd;

// imports:
import ibs.app.AppFunctions;
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.bo.BOArguments;
import ibs.bo.BOMessages;
import ibs.bo.Buttons;
import ibs.bo.Operations;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.ButtonBarElement;
import ibs.util.list.IElementId;

import m2.bbd.BbdFunctions;
import m2.bbd.DiscussionEntry;
import m2.bbd.Discussion_01;


/******************************************************************************
 * Application object which is created with each call of a page. <BR/>
 * An object of this class represents the interface between the network and the
 * business logic itself. <BR/>
 * It gets arguments from the user, controls the program flow, and sends data
 * back to the user and his browser. <BR/>
 * There has to be generated an extension class of this class to realize the
 * functions which are specific to the required application.
 *
 * @version     $Id: BbdFunctionHandler.java,v 1.64 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980402
 ******************************************************************************
 */
public class BbdFunctionHandler extends GeneralFunctionHandler
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: BbdFunctionHandler.java,v 1.64 2010/04/07 13:37:10 rburgermann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a BbdFunctionHandler object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   id      Id of the element.
     * @param   name    The element's name.
     */
    public BbdFunctionHandler (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // initialize the other instance properties:
    } // BbdFunctionHandler


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
        Discussion_01 disc;

        try
        {
            switch (function)       // perform function
            {
                case BbdFunctions.FCT_DISCCHANGESTATE: // change the state of the discussion
                    disc = (Discussion_01) values.getObject ();
                    disc.changeState (values.p_env.getOidParam (BOArguments.ARG_TABID));
                    break;

                // show the quickview of objects
                case BbdFunctions.FCT_DISC_QUICKVIEW:
                    // handle the different types of discussion entries:
                    DiscussionEntry entry =
                        (DiscussionEntry) values.getObject ();
                    if (entry != null) // got object?
                    {
                        entry.quickView (1);
                    } // if got object
                    break;

                // show phone book search form
                case BbdFunctions.FCT_NQUICKANSWER :
/*
                        discEntry = (Beitrag_01) getObject (oid);
*/
                    IOHelpers.showMessage (MultilingualTextProvider
                        .getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_NOTIMPLEMENTED, values.p_env),
                        values.p_app, values.p_sess, values.p_env);
                    // AppFunctions.FCT_OBJECTNEWFORM --> when there.
                    break;

                default:            // unknown function
                    // return the function to be performed:
                    return function;
            } // switch function
        } // try
        catch (Exception e)
        {
            IOHelpers.showMessage (e.toString (),
                values.p_app, values.p_sess, values.p_env);
        } // catch
//trace ("BbdFunctionHandler.evalFunction end");

        // return that no further function shall be performed:
        return AppFunctions.FCT_NOFUNCTION;
    } // evalFunction


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     */
    public void setButtons (ButtonBarElement buttonBar)
    {
        final String funcLoadCont =
            IOHelpers.getLoadContJavaScript (AppFunctions.FCT_OBJECTNEWFORM);

        // call corresponding method of super class:
        super.setButtons (buttonBar);

        // new topic:
        this.setButton (buttonBar, Buttons.BTN_TOPICNEW, funcLoadCont,
            Operations.OP_NEW | Operations.OP_ADDELEM, 0);

        // answer:
        this.setButton (buttonBar, Buttons.BTN_ANSWER, funcLoadCont,
            Operations.OP_NEW | Operations.OP_ADDELEM, 0);
    } // setButtons

} // class BbdFunctionHandler
